package com.paavocode.customer;

import org.springframework.stereotype.Service;

import com.paavocode.amqp.RabbitMQMessageProducer;
import com.paavocode.clients.fraud.FraudCheckResponse;
import com.paavocode.clients.fraud.FraudClient;
import com.paavocode.clients.notification.NotificationRequest;

@Service
public record CustomerService(CustomerRepository customerRepository, FraudClient fraudClient,
		RabbitMQMessageProducer rabbitMQMessageProducer) {

	public void registerCustomer(CustomerRegistrationRequest request) {
		Customer customer = Customer.builder().firtsName(request.firstName()).lastName(request.lastName())
				.email(request.email()).build();

		customerRepository.saveAndFlush(customer);

//		FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
//				"http://FRAUD/api/v1/fraud-check/{customerId}", FraudCheckResponse.class, customer.getId());

		FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

		if (fraudCheckResponse.isFraudster()) {
			throw new IllegalStateException("frauster");
		}

//		notificationClient.sendNotification(new NotificationRequest(customer.getId(), customer.getEmail(),
//				String.format("Ola %s, bem vindo ao Paavo code", customer.getFirtsName())));

		NotificationRequest notificationRequest = new NotificationRequest(customer.getId(), customer.getEmail(),
				String.format("Ola %s, bem vindo ao Paavo code", customer.getFirtsName()));

		rabbitMQMessageProducer.publish(notificationRequest, "internal.exchange", "internal.notification.routing-key");

	}

}
