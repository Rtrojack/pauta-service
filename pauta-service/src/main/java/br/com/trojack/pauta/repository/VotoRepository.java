package br.com.trojack.pauta.repository;

import br.com.trojack.pauta.entity.Voto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VotoRepository extends MongoRepository<Voto, String> {
    List<Voto> findByIdPauta(String idPauta);

    Optional<Voto> findByIdPautaAndCpf(String idPauta, String cpf);
}
