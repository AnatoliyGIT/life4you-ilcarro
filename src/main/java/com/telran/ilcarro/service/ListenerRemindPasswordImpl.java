package com.telran.ilcarro.service;

import com.telran.ilcarro.exception.ActionDeniedException;
import com.telran.ilcarro.exception.NotFoundException;
import com.telran.ilcarro.model.documents.ListenerRemind;
import com.telran.ilcarro.repository.ListenerRemindPasswordRepository;
import com.telran.ilcarro.repository.UserRepository;
import com.telran.ilcarro.service.interfaces.ListenerRemindPassword;
import com.telran.ilcarro.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ListenerRemindPasswordImpl implements ListenerRemindPassword {
    private ListenerRemindPasswordRepository listenerRemindPassword;
    private UserRepository userRepository;

    @Autowired
    public ListenerRemindPasswordImpl(ListenerRemindPasswordRepository listenerRemindPassword
            , UserRepository userRepository) {
        this.listenerRemindPassword = listenerRemindPassword;
        this.userRepository = userRepository;
    }

    @Override
    public void getRemindPassword(String email, String host) {
        if (userRepository.findUserByEmail(email) == null) throw new NotFoundException("User with email not found!");
        List<LocalDateTime> list = new ArrayList<>();
        Optional<ListenerRemind> listener = listenerRemindPassword.findById(email);
        if (listener.isPresent()) {
            for (LocalDateTime times : listener.get().getRemind_time()) {
                long time = Utils.getHoursTotalBetweenTwoDates(times, LocalDateTime.now());
                if (time <= 12) {
                    throw new ActionDeniedException("Access limit remind password email : "
                            + email + " and host : " + host + " to 24 hours!");
                }
            }
            list.add(LocalDateTime.now());
            listener.get().setRemind_time(list);
        }
        if (!listener.isPresent()){
            list.add(LocalDateTime.now());
           listener = Optional.ofNullable(ListenerRemind.builder().email(email).remind_time(list).build());
        }
        listenerRemindPassword.save(listener.get());
    }
}
