package com.example.Server_electronic_journale.controller;

import com.example.Server_electronic_journale.model.Group;
import com.example.Server_electronic_journale.repository.GroupRepository;
import com.example.Server_electronic_journale.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupService groupService;

    public Group addGroup(String name) {
        // Проверка на уникальность имени группы
        if (groupRepository.existsByName(name)) {
            throw new IllegalArgumentException("Группа с таким названием уже существует");
        }
        Group group = new Group();
        group.setName(name);
        return groupRepository.save(group);
    }

    @GetMapping
    public List<Group> getGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{groupId}")
    public Group getGroupById(@PathVariable int groupId) {
        return groupService.getGroupById(groupId);
    }
}