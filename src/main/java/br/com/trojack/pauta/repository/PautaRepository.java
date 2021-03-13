package br.com.trojack.pauta.repository;

import br.com.trojack.pauta.entity.Pauta;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PautaRepository extends MongoRepository<Pauta, String> {
}

