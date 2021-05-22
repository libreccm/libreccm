/*
 * Copyright (C) 2021 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.librecms.ui.contentsections.assets;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.assets.PostalAddress;
import org.librecms.contentsection.AssetRepository;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsPostalAddressCreateStep")
public class PostalAddressCreateStep
    extends AbstractMvcAssetCreateStep<PostalAddress> {

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private String address;

    private String postalCode;

    private String city;

    private String state;

    private String isoCountryCode;

    @Override
    public String showCreateStep() {
        return "org/librecms/ui/contentsection/assets/postaladdress/create-postaladdress.xhtml";
    }

    @Override
    protected Class<PostalAddress> getAssetClass() {
        return PostalAddress.class;
    }

    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    protected String setAssetProperties(
        final PostalAddress postalAddress,
        final Map<String, String[]> formParams
    ) {
        address = Optional
            .ofNullable(formParams.get("address"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse(null);
        postalCode = Optional
            .ofNullable(formParams.get("postalCode"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse(null);
        city = Optional
            .ofNullable(formParams.get("city"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse(null);
        state = Optional
            .ofNullable(formParams.get("state"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse(null);
        isoCountryCode = Optional
            .ofNullable(formParams.get("isoCountryCode"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse(null);

        postalAddress.setAddress(address);
        postalAddress.setPostalCode(postalCode);
        postalAddress.setCity(city);
        postalAddress.setState(state);
        postalAddress.setIsoCountryCode(isoCountryCode);

        assetRepo.save(postalAddress);

        return String.format(
            "redirect:/%s/assets/%s/%s/@postaladdress-edit",
            getContentSectionLabel(),
            getFolderPath(),
            getName()
        );
    }

    @Override
    public String getAssetType() {
        return PostalAddress.class.getName();
    }

    @Override
    public String getLabel() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("postal_address.label");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("postal_address.description");
    }

    @Override
    public String getBundle() {
        return MvcAssetStepsConstants.BUNDLE;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    public Map<String, String> getCountries() {
        final Set<Locale> countries = Arrays
            .stream(Locale.getISOCountries())
            .map(country -> new Locale("", country))
            .collect(Collectors.toCollection(LinkedHashSet::new));

        final Map<String, String> countriesMap = countries
            .stream()
            .sorted(this::sortCountries)
            .collect(
                Collectors.toMap(
                    Locale::getCountry,
                    locale -> locale.getDisplayCountry(
                        globalizationHelper.getNegotiatedLocale()
                    ),
                    (value1, value2) -> value1,
                    LinkedHashMap::new
                )
            );

        final Map<String, String> values = new LinkedHashMap<>();
        values.put("", "");
        values.putAll(countriesMap);

        return values;
    }

    private int sortCountries(final Locale locale1, final Locale locale2) {
        final Locale negotiated = globalizationHelper.getNegotiatedLocale();
        final String country1 = locale1.getDisplayCountry(negotiated);
        final String country2 = locale2.getDisplayCountry(negotiated);

        return country1.compareTo(country2);
    }

}
