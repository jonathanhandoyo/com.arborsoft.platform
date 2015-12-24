package com.arborsoft.platform.web.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "users")
public class User {
    @Id
    String id;

    String username;
    String credential;

    String name;

    String[] roles;
}
