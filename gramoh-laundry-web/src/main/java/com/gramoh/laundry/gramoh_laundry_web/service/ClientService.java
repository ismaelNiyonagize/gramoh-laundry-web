package com.gramoh.laundry.gramoh_laundry_web.service;

import com.gramoh.laundry.gramoh_laundry_web.model.Client;
import com.gramoh.laundry.gramoh_laundry_web.model.User;
import com.gramoh.laundry.gramoh_laundry_web.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client saveClient(Client client, User user) {
        client.setUser(user);
        return clientRepository.save(client);
    }

    public Optional<Client> getClientByUser(User user) {
        return clientRepository.findByUser(user);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public Client updateClient(Long id, Client updatedClient) {
        return clientRepository.findById(id).map(client -> {
            client.setFullName(updatedClient.getFullName());
            client.setPhone(updatedClient.getPhone());
            client.setAddress(updatedClient.getAddress());
            return clientRepository.save(client);
        }).orElseThrow(() -> new RuntimeException("Client not found"));
    }


}
