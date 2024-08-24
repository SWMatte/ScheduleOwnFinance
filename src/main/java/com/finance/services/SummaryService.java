package com.finance.services;

import com.finance.entities.Summary;
import com.finance.repositories.SummaryRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SummaryService {

    @Autowired
    private final SummaryRepository summaryRepository;


    //TODO: aggiorna la lista con i controlli + fai le altre query a db per le tabelle, una per il devito , una che richiama la finanza dispo 3 tabelle a frontend nella home
    //TODO: vedi se aggiungere categorie come parametri
    public List<Summary> getAllValues(){

        return  summaryRepository.findAll();

    }

}
