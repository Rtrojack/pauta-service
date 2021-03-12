package br.com.trojack.pauta.repository;

import br.com.trojack.pauta.entity.Voto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VotoRepository extends MongoRepository<Voto, String> {
    Optional<Voto> findByIdPautaAndCpf(String idPauta, String cpf);
}
