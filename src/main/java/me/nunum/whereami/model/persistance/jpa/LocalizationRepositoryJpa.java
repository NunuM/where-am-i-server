package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.persistance.LocalizationRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

public class LocalizationRepositoryJpa
        extends JpaRepository<Localization, Long>
        implements LocalizationRepository {

    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }

    @Override
    public List<Localization> paginate(Optional<Integer> page) {

        entityManager().close();

        List<Localization> localizations = new ArrayList<>();

        final Integer currentPage = page.map(p -> {
            if (p < 1) {
                return 1;
            } else return p;
        }).orElse(1);

        final Iterator<Localization> theLocalizationIterator = this.iterator(currentPage);

        theLocalizationIterator.forEachRemaining(localizations::add);

        Collections.sort(localizations);

        return localizations;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Localization> searchWithPagination(Device device, Optional<Integer> page, Optional<String> localizationName) {

        final Integer currentPage = page.map(p -> {
            if (p < 1) {
                return 1;
            } else return p;
        }).orElse(1);

        final EntityManager manager = entityManager();

        if (!(localizationName.isPresent())) {
            return (List<Localization>) manager.createNamedQuery("Localization.allVisibleLocalizations")
                    .setParameter("ownerId", device.getId())
                    .setMaxResults(DEFAULT_PAGESIZE)
                    .setFirstResult((currentPage - 1) * DEFAULT_PAGESIZE)
                    .getResultList();
        } else {
            final String lName = localizationName.get();

            return (List<Localization>) manager.createNamedQuery("Localization.allVisibleLocalizationsFilterByName")
                    .setParameter("ownerId", device.getId())
                    .setParameter("name", lName)
                    .setMaxResults(DEFAULT_PAGESIZE)
                    .setFirstResult((currentPage - 1) * DEFAULT_PAGESIZE)
                    .getResultList();
        }
    }
}
