package com.gramoh.laundry.gramoh_laundry_web.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Client {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;
    private String address;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;



    @ManyToOne
    @JoinColumn(name = "package_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)


    private Package subscribedPackage;


}
