package com.tetraquimica.auth_api.models;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Document(collectionName = "usuarios")
public class Usuario {

    @DocumentId
    private String id;  // ID do Firestore

    private String nome;
    private String email;
    private String senha;  // 🔹 Armazenar senha hashada (criptografada)
    private Role role;

    public enum Role {
        ADMIN("ADMIN"),
        USER("USER");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
