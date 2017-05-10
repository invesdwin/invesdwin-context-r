package de.invesdwin.context.r.runtime.renjin;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class RenjinProperties {

    private static final Set<String> EXTENDED_PACKAGE_SEARCH_GROUP_IDS = Collections
            .synchronizedSet(new LinkedHashSet<String>());

    static {
        EXTENDED_PACKAGE_SEARCH_GROUP_IDS.add("de.invesdwin.context.r.runtime.renjin.packages");
    }

    private RenjinProperties() {}

    public static Set<String> getExtendedPackageSearchGroupIds() {
        return Collections.unmodifiableSet(EXTENDED_PACKAGE_SEARCH_GROUP_IDS);
    }

    public static boolean addExtendedPackageSearchGroupId(final String groupId) {
        return EXTENDED_PACKAGE_SEARCH_GROUP_IDS.add(groupId);
    }

}
