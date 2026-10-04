package com.example.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.contracts.user.GetUserReply;
import com.example.order.client.UserClient;
import com.example.order.client.UserNotFoundException;
import com.example.order.event.OrderCreatedEvent;
import com.example.order.event.OrderEventPublisher;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock UserClient userClient;
    @Mock OrderRepository repository;
    @Mock OrderEventPublisher publisher;
    @InjectMocks OrderService service;

    @Test
    void createsOrderAndPublishesEvent() {
        when(userClient.getUser(1L)).thenReturn(
                GetUserReply.newBuilder().setId(1).setName("Asha").setEmail("asha@example.com").build());
        when(repository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse res = service.create(new CreateOrderRequest(1L, new BigDecimal("49.99")));

        assertEquals("CREATED", res.status());
        ArgumentCaptor<OrderCreatedEvent> captor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(publisher).publish(captor.capture());
        assertEquals("Asha", captor.getValue().userName());
    }

    @Test
    void unknownUserSavesNothingAndPublishesNothing() {
        when(userClient.getUser(2L)).thenThrow(new UserNotFoundException(2L));

        assertThrows(UserNotFoundException.class,
                () -> service.create(new CreateOrderRequest(2L, new BigDecimal("10.00"))));

        verifyNoInteractions(repository, publisher);
    }
}
