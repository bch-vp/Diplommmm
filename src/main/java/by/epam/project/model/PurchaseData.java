package by.epam.project.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;

/**
 * The type Order.
 */
public class PurchaseData {
    /**
     * The enum Status.
     */

    private java.sql.Date dateCreatedAt;
    private Integer count;

    public PurchaseData(Date dateCreatedAt, Integer count) {
        this.dateCreatedAt = dateCreatedAt;
        this.count = count;
    }

    public Date getDateCreatedAt() {
        return dateCreatedAt;
    }

    public void setDateCreatedAt(Date dateCreatedAt) {
        this.dateCreatedAt = dateCreatedAt;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseData that = (PurchaseData) o;
        return Objects.equals(dateCreatedAt, that.dateCreatedAt) && Objects.equals(count, that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateCreatedAt, count);
    }
}
