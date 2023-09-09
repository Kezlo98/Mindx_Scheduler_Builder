package com.sonthai.schedulermanagement.entity;

import com.sonthai.schedulermanagement.constant.TaskStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Task {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String content;

    private String assignee;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime time;

    @Enumerated(EnumType.ORDINAL)
    private TaskStatusEnum status;

    private boolean isImportant;
    @ElementCollection
    private Set<String> links = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @PrePersist
    private void prePersist(){
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate(){
        updatedDate = LocalDateTime.now();
    }
}
