/**
 * 
 */
package HongZe.MyChatRoom.entity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;

/**
 * The Super Entity Abstract Class
 */
@MappedSuperclass
public abstract class AbstractEntity {
	private Long id;
	private long createdAt;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = false, updatable = false)
	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	@Transient
	public ZonedDateTime getCreatedDateTime() {
		// TODO Auto-generated method stub
		return Instant.ofEpochMilli(this.createdAt).atZone(ZoneId.systemDefault());
	}

	@PrePersist
	public void preInsert() {
		// TODO Auto-generated method stub
		setCreatedAt(System.currentTimeMillis());
	}
}
