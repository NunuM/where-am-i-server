package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.persistance.LocalizationRepository;
import me.nunum.whereami.utils.AppConfig;

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

    @Override
    public List<Localization> searchWithPagination(Optional<Integer> page, Optional<String> localizationName) {

        if (!localizationName.isPresent()) {
            return this.paginate(page);
        }

        final Integer currentPage = page.map(p -> {
            if (p < 1) {
                return 1;
            } else return p;
        }).orElse(1);

        final String localizationLabel = localizationName.get();

        final CriteriaBuilder criteriaBuilder = super.entityManager().getCriteriaBuilder();

        final CriteriaQuery<Localization> criteriaQuery = criteriaBuilder.createQuery(Localization.class);

        final Root<Localization> localizationRoot = criteriaQuery.from(Localization.class);

        final CriteriaQuery<Localization> query = criteriaQuery
                .select(localizationRoot)
                .where(criteriaBuilder.like(localizationRoot.get("label"), "%" + localizationLabel + "%"))
                .orderBy(criteriaBuilder.desc(localizationRoot.get("created")));

        return super.pageWithFiltering(query, currentPage);
    }
}
