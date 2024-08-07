package models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name="inboxes")
@Getter @Setter
public class Inbox extends ModelBase {

    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;

    private String title;

    @Column(length=100000)
    @Lob
    private String message;

    @Column(nullable=false)
    private boolean seen;

}
