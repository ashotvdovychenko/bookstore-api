package com.example.bookstore.api.repository;

import com.example.bookstore.api.domain.Book;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends R2dbcRepository<Book, UUID> {}
