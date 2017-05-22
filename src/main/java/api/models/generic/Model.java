package api.models.generic;

import java.io.Serializable;

/**
 * Базовый класс всех моделей
 */
public abstract class Model<ID extends Serializable> {

    public abstract ID getId();


    public boolean isNew() {
        return getId() == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Model)) return false;

        final Model that = (Model) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}

