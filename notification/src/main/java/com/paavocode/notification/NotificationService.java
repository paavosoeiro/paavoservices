package com.paavocode.notification;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.paavocode.clients.notification.NotificationRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public void send(NotificationRequest notificationRequest) {
		notificationRepository.save(Notification.builder().toCustomerId(notificationRequest.toCustomerId())
				.toCustomerEmail(notificationRequest.toCustomerName()).sender("PaavoCode")
				.message(notificationRequest.message()).sentAt(LocalDateTime.now()).build());
	}
}
