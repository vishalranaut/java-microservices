package com.example.order;

import com.example.contracts.user.GetUserReply;
import com.example.order.client.UserClient;
import com.example.order.event.OrderCreatedEvent;
import com.example.order.event.OrderEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final UserClient userClient;
    private final OrderRepository repository;
    private final OrderEventPublisher publisher;

    public OrderService(UserClient userClient, OrderRepository repository, OrderEventPublisher publisher) {
        this.userClient = userClient;
        this.repository = repository;
        this.publisher = publisher;
    }

    /**
     * Not @Transactional on purpose: the gRPC call must not hold a DB connection.
     * Known gap: save-then-publish is a dual write. See the outbox pattern in the README.
     */
    public OrderResponse create(CreateOrderRequest req) {
        GetUserReply user = userClient.getUser(req.userId());
        Order saved = repository.save(new Order(req.userId(), req.amount()));
        publisher.publish(new OrderCreatedEvent(
                saved.getId(), saved.getUserId(), user.getName(), saved.getAmount(), saved.getCreatedAt()));
        return OrderResponse.from(saved);
    }

    public OrderResponse get(Long id) {
        return repository.findById(id).map(OrderResponse::from).orElseThrow(() -> new OrderNotFoundException(id));
    }
}
