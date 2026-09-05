package hexlet.code.model;

import java.sql.Timestamp;

public class Url {

    private Long id;
    private String name;
    private Timestamp createdAt;

    private Integer lastCheckStatusCode;
    private Timestamp lastCheckCreatedAt;

    public Url(String name) {
        this.name = name;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Url(Long id, String name, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Url(
            Long id,
            String name,
            Timestamp createdAt,
            Integer lastCheckStatusCode,
            Timestamp lastCheckCreatedAt
    ) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.lastCheckStatusCode = lastCheckStatusCode;
        this.lastCheckCreatedAt = lastCheckCreatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getLastCheckStatusCode() {
        return lastCheckStatusCode;
    }

    public void setLastCheckStatusCode(Integer lastCheckStatusCode) {
        this.lastCheckStatusCode = lastCheckStatusCode;
    }

    public Timestamp getLastCheckCreatedAt() {
        return lastCheckCreatedAt;
    }

    public void setLastCheckCreatedAt(Timestamp lastCheckCreatedAt) {
        this.lastCheckCreatedAt = lastCheckCreatedAt;
    }
}