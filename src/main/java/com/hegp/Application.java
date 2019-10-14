package com.hegp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wenhao.jpa.Specifications;
import com.hegp.core.jpa.SQLRepository;
import com.hegp.entity.UserEntity;
import com.hegp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.*;

@SpringBootApplication
public class Application implements CommandLineRunner {
    @Autowired
    private UserService userService;
    @Autowired
    private SQLRepository sqlRepository;

    private ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        UserEntity userEntity = testSaveUser();
        testDeleteUser(userEntity);
        testSaveUser();
        testQueryUser1();
        testQueryUser2();
        testQueryUser3();
        testSqlQuery();
    }

    private UserEntity testSaveUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setDel(false);
        userEntity.setPhone("phone1");
        userEntity.setNickname("nickname1");
        userEntity.setUsername("username1");
        userService.save(userEntity);
        return userEntity;
    }

    private void testDeleteUser(UserEntity userEntity) {
        if (userEntity!=null) {
            userService.delete(userEntity);
        }
    }

    private void testQueryUser1() throws JsonProcessingException {
        Specification<UserEntity> specification = Specifications.<UserEntity>and()
                .like("username", "%a%")
                .like("phone", "%%a")
                .like("nickname", "%a%")
                .build();
        List<UserEntity> users = userService.getRepository().findAll(specification);
        System.out.println(mapper.writeValueAsString(users));
    }

    private void testQueryUser2() throws JsonProcessingException {
        String usernameCondition = "";
        String condition = "";
        Specification specification = Specifications.and()
                .like(StringUtils.hasText(usernameCondition), "username", "%"+usernameCondition+"%")
                .predicate(StringUtils.hasText(condition), Specifications.or()
                        .like("phone", "%"+condition+"%")
                        .like("nickname", "%"+condition+"%")
                        .build())
                .build();
        List<UserEntity> users = userService.getRepository().findAll(specification);
        System.out.println(mapper.writeValueAsString(users));
    }

    private void testQueryUser3() throws JsonProcessingException {
        Specification specification = Specifications.and()
                .in("id", "1","2","3","4")
                .like("username", "%a%")
                .predicate(Specifications.or()
                        .like("phone", "%a%")
                        .like("nickname", "%a%")
                        .build())
                .build();
        List<UserEntity> users = userService.getRepository().findAll(specification);
        System.out.println(mapper.writeValueAsString(users));
    }

    private void testSqlQuery() {
        String sql = " SELECT su.id, su.username, su.nickname, su.phone, su.del, sr.id role_id, sr.name role_name FROM sys_user_role_rel surr " +
                " LEFT JOIN sys_user su ON surr.user_id = su.id " +
                " LEFT JOIN sys_role sr ON surr.role_id = sr.id " +
                " WHERE su.del=? ";
        sqlRepository.queryPageResultList(sql,1,1, false);
        try {
            System.out.println(mapper.writeValueAsString(sqlRepository.queryResultList("SELECT id, username FROM sys_user")));
            List<String> list = Arrays.asList("00","11","22","33","44");
            String id = "0000";
            System.out.println(mapper.writeValueAsString(sqlRepository.queryResultList("SELECT id, username FROM sys_user WHERE id IN ?1 AND id=?2", list, id)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        sqlRepository.queryResultCount("SELECT COUNT(id) FROM sys_user");
    }
}

