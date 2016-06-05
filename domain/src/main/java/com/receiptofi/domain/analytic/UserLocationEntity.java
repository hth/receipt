package com.receiptofi.domain.analytic;

import com.receiptofi.domain.BaseEntity;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 6/4/16 11:09 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "A_USER_LOCATION")
@CompoundIndexes (value = {
        @CompoundIndex (name = "user_location_idx", def = "{'RID': 1}", unique = true, background = true)
})
public class UserLocationEntity extends BaseEntity {

//    @Field ("RID")
//
//    @Field ("storeAdd")
//            "lng"
//            "lat"
//            "visitSetGroupMax::maxVisit"
}
