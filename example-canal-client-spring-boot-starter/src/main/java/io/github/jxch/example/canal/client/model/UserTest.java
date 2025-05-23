package io.github.jxch.example.canal.client.model;

import io.github.jxch.canal.client.name.CanalColumn;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserTest {
    private Long id;
    @CanalColumn(name = "username")
    private String name;
    private String email;
    @CanalColumn(name = "created_at")
    private Date createTime;
}
