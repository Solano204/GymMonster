package infraestrucutre.Adapters.Drivens.ImpServices;
import org.springframework.stereotype.Service;

import application.Ports.Drivers.IServices.SpecialtyServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Membership;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import infraestrucutre.Adapters.Drivens.Repositories.MembershipRepository;
import infraestrucutre.Adapters.Drivens.Repositories.PromotionRepository;
import infraestrucutre.Adapters.Drivens.Repositories.SpecialtyRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class SpecialtyService implements SpecialtyServiceInterface {

    private final SpecialtyRepository specialtyRepository;

    @Override
    public Mono<Specialty> createSpecialty(Specialty specialty) {
        return specialtyRepository.save(specialty);
    }

    @Override
    public Flux<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }

    @Override
    public Mono<Specialty> getSpecialtyById(Integer id) {
        return specialtyRepository.findById(id);
    }

    @Override
    public Mono<Specialty> getSpecialtyByName(String name) {
        return specialtyRepository.findByName(name);
    }

    @Override
    public Mono<Specialty> updateSpecialty(Integer id, Specialty specialty) {
        return specialtyRepository.findById(id)
                .flatMap(existingSpecialty -> {
                    existingSpecialty.setName(specialty.getName());
                    existingSpecialty.setDescription(specialty.getDescription());
                    return specialtyRepository.save(existingSpecialty);
                });
    }

    @Override
    public Mono<Specialty> updateSpecialtyName(String name,Specialty specialty) {
        return specialtyRepository.findByName(name)
                .flatMap(existingSpecialty -> {
                    existingSpecialty.setName(specialty.getName());
                    existingSpecialty.setDescription(specialty.getDescription());
                    return specialtyRepository.save(existingSpecialty);
                });
    }

    @Override
    public Mono<Void> deleteSpecialty(Integer id) {
        return specialtyRepository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteSpecialtyByName(String name) {
        return specialtyRepository.deleteByName(name);
    }
}
