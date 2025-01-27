package com.Trainee.ConectaTraineeBackend.model;


import jakarta.persistence.*;

@Entity
@Table(name = "projetos")

public class Projetos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;


}
