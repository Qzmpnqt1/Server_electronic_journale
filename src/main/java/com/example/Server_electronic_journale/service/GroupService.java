package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.model.Group;
import com.example.Server_electronic_journale.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    public Group addGroup(String name) {
        // Проверка на уникальность имени
        if (groupRepository.existsByName(name)) {
            throw new IllegalArgumentException("Группа с таким названием уже существует");
        }
        Group group = new Group();
        group.setName(name);
        return groupRepository.save(group);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(int groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + groupId));
    }
}