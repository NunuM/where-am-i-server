package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.persistance.LocalizationRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class LocalizationRepositoryJpa
        extends JpaRepository<Localization, Long>
        implements LocalizationRepository {

    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }


    @Override
    public void deleteLocalization(Localization localization) {
        final EntityManager entityManager = entityManager();

        entityManager.getTransaction();

        entityManager.createQuery("").executeUpdate();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Localization> searchWithPagination(Device device,
                                                   Optional<Integer> page,
                                                   Optional<String> localizationName,
                                                   Optional<String> trained) {

        final Integer currentPage = page.map(p -> {
            if (p < 1) {
                return 1;
            } else return p;
        }).orElse(1);

        final EntityManager manager = entityManager();

        if (trained.isPresent()) {
            return (List<Localization>) manager.createNamedQuery("Localization.allVisibleLocalizationsFilterByTraining")
                    .setParameter("ownerId", device.getId())
                    .setMaxResults(DEFAULT_PAGE_SIZE)
                    .setFirstResult((currentPage - 1) * DEFAULT_PAGE_SIZE)
                    .getResultList();
        }


        if (!(localizationName.isPresent())) {
            return (List<Localization>) manager.createNamedQuery("Localization.allVisibleLocalizations")
                    .setParameter("ownerId", device.getId())
                    .setMaxResults(DEFAULT_PAGE_SIZE)
                    .setFirstResult((currentPage - 1) * DEFAULT_PAGE_SIZE)
                    .getResultList();
        } else {
            final String lName = localizationName.get();

            return (List<Localization>) manager.createNamedQuery("Localization.allVisibleLocalizationsFilterByName")
                    .setParameter("ownerId", device.getId())
                    .setParameter("name", lName)
                    .setMaxResults(DEFAULT_PAGE_SIZE)
                    .setFirstResult((currentPage - 1) * DEFAULT_PAGE_SIZE)
                    .getResultList();
        }
    }
}
