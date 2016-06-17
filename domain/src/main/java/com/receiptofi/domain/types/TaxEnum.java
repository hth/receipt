/**
 *
 */
package com.receiptofi.domain.types;

/**
 * @author hitender
 * @since Dec 27, 2012 1:22:39 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum TaxEnum {

    T("T", "Taxed"),
    NT("NT", "Not Taxed");

    private final String description;
    private final String name;

    TaxEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
