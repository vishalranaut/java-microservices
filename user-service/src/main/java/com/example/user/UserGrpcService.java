package com.example.user;

import com.example.contracts.user.GetUserReply;
import com.example.contracts.user.GetUserRequest;
import com.example.contracts.user.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

/** gRPC entry point used by other services. Reuses UserService, so it shares the same cache. */
@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService users;

    public UserGrpcService(UserService users) {
        this.users = users;
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserReply> observer) {
        try {
            UserDto user = users.get(request.getId());
            observer.onNext(GetUserReply.newBuilder()
                    .setId(user.id())
                    .setName(user.name())
                    .setEmail(user.email())
                    .build());
            observer.onCompleted();
        } catch (UserNotFoundException e) {
            observer.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
