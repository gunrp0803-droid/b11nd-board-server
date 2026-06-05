package com.example.b11ndboard.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 이 클래스를 상속받는 자식 클래스들에게 아래 필드를 테이블 컬럼으로 넘겨줘요
@EntityListeners(AuditingEntityListener.class) // 시간에 대해 자동으로 감시(Auditing)하겠다는 뜻
public abstract class BaseTimeEntity {

    @CreatedDate // 데이터가 처음 저장(Insert)될 때 시간이 자동으로 들어가요
    @Column(updatable = false) // 생성 시간은 수정되면 안 되니까 보호!
    private LocalDateTime createdAt;

    @LastModifiedDate // 데이터가 수정(Update)될 때마다 시간이 자동으로 바뀌어요
    private LocalDateTime updatedAt;
}