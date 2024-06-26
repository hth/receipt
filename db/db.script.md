### Update billing query

Billing Account update to Promotional, set to "P"
    
    db.getCollection('BILLING_ACCOUNT').find({BP : {$ne : "P"}})
    
    db.getCollection('BILLING_HISTORY').find({"BS" : { $ne : "P"}})
    db.getCollection('BILLING_HISTORY').updateMany({BS: {$ne : 'P'}}, { $set: {BS: 'P', BP: 'P'}})
        
    db.getCollection('RECEIPT').find({"BS": {$ne : "P"}})
    db.getCollection('RECEIPT').update({"BS" : "NB"},   { $set: {BS: 'P', U : new ISODate()} }, {multi: true});

### Date Dec 6 2016 - Build 2012
Remove index for
 
    ForgotRecover
    Friend
    Invite
    
Auto add index for
 
    UserPreference

### Date Nov 19 2016 - Build 1964

Forced update Expense Tag 

    db.getCollection('EXPENSE_TAG').update(
        {"TAG" : "HOME", "IC" : "V100"},   
        { $set: {IC: "V101", U : new ISODate()} }, 
        {multi: true}
    );
    
    db.getCollection('EXPENSE_TAG').update(
        {"TAG" : "BUSINESS", "IC" : "V100"},   
        { $set: {IC: "V102", U : new ISODate()} }, 
        {multi: true}
    );

### Date Nov 8 20016 - Build 1946

Missed changing ULE to UL for MessageDocumentEntity. There was no adverse effect.

    db.getCollection('MESSAGE_DOCUMENT').updateMany({}, {$rename: { "ULE": "UL" }})

### Date Oct 9 2016 - Build 1876

    db.getCollection('EXPENSE_TAG').updateMany({}, {$set: {IC: "V100"} })
    db.getCollection('BILLING_ACCOUNT').updateMany({}, {$rename: { "ABT": "BP" }})
    db.getCollection('BILLING_HISTORY').updateMany({}, {$rename: { "ABT": "BP" }})
    
Billing Account update to Promotional, set to "P" -- On OCT 13, 2016
    
    db.getCollection('BILLING_ACCOUNT').find({ABT : {$ne : "P"}})
    
    db.getCollection('BILLING_HISTORY').find({"BS" : { $ne : "P"}})
    db.getCollection('BILLING_HISTORY').updateMany({BS: {$ne : 'P'}}, { $set: {BS: 'P', ABT: 'P'}})
        
    db.getCollection('RECEIPT').find({"BS": {$ne : "P"}})
    db.getCollection('RECEIPT').update({"BS" : "NB"},   { $set: {BS: 'P', U : new ISODate()} }, {multi: true});

##### After Build 1876 End

Deployment:
    Migrate from Server build 1795 to 1840 and Mobile Server build 1780 to 1820 

    brew update
    brew info activemq
    brew install activemq
    brew doctor
    brew cleanup
    
    db.getCollection('RECEIPT').dropIndex("receipt_unique_idx")
    db.getCollection('RECEIPT').updateMany({}, {$rename: { "CS": "CZ" }})
    
    db.getCollection('RECEIPT').updateMany({}, {$rename: { "CN": "NO" }})
    db.getCollection('DOCUMENT').updateMany({}, {$rename: { "CN": "NO" }})
    
    db.getCollection('BIZ_STORE').find().snapshot().forEach(
      function (e) {           
        // update document, using its own properties
        e.FA = e.AD;
    
        // save the updated document
        db.getCollection('BIZ_STORE').save(e);
      }
    )

Complete

    - Data base removed and updated business
    
            Added business South Carolina Education Lottery
            
            Found Biz_Store id and removed bad store address sceducationlottry.com
            
            Update Paytm.com to map to Paytm and then delete Paytm.com


### Date Sept 6 2016 - Build 1832
- Brew update activemq to 5.14.0  

        brew update
        brew info activemq
        brew install activemq
        brew doctor
        brew cleanup
        
        Add business    - South Carolina Education Lottery
        Address         - 1309 Assembly Street Columbia, SC 29201
        Phone           - 803-253-4004
        
        - Find and confirm BIZ_STORE id
        
        db.getCollection('RECEIPT').find(
            {
                "BIZ_NAME.$id" : ObjectId("5755eaa6f4a3b6c5ce89b5dc"), 
                "BIZ_STORE.$id" : ObjectId("5755eaa6f4a3b6c5ce89b5dd")
            }
        )
        
        - Update the BizName and BizStore for this record with new record thats created
        
        db.getCollection('BIZ_NAME').find({"N" : "South Carolina Education Lottery"})
        db.getCollection('BIZ_STORE').find({"BIZ_NAME.$id" : ObjectId("57d0dc3df4a3b633b9313cb2")})
        
        BIZ_NAME id - 57d0dc3df4a3b633b9313cb2
        BIZ_STORE id - 57d0dc3df4a3b633b9313cb3
        
        Replace Receipt with BIZ_STORE id - 5755eaa6f4a3b6c5ce89b5dd
        
        Get Ids and replace with existing "South carolina education lottery"
        
        - And then remove BizStore or delete from website 
        
        Then remove BIZ_STORE 5755eaa6f4a3b6c5ce89b5dd
        db.getCollection('RECEIPT').remove({"_id" : ObjectId("5755eaa6f4a3b6c5ce89b5dd")});

- Drop index, this will be recreated. CS is indexed. Hence needs to dropped first before changing field name.
        
        db.getCollection('RECEIPT').dropIndex("receipt_unique_idx")
    
- CS was checksum. Changed to CZ.

        db.getCollection('RECEIPT').updateMany({}, {$rename: { "CS": "CZ" }})

- Update Paytm.com to map to Paytm and then delete Paytm.com
    
        db.getCollection('BIZ_NAME').find({N:"Paytm"})
        db.getCollection('BIZ_NAME').find({N:"Paytm.com"})
        
        db.getCollection('RECEIPT').find({"BIZ_NAME.$id" : ObjectId("564c07aff4a3b6d5de8b2ec8")}) 
        db.getCollection('RECEIPT').find({"BIZ_NAME.$id" : ObjectId("5624d1c9f4a3b633386ad5e2")}) 
        db.getCollection('RECEIPT').update(
            {"BIZ_NAME.$id" : ObjectId("5624d1c9f4a3b633386ad5e2")},   
            {$set: {"BIZ_NAME.$id" : ObjectId("564c07aff4a3b6d5de8b2ec8")} }, 
            {multi: true});
            
        db.getCollection('DOCUMENT').find({"BIZ_NAME.$id" : ObjectId("5624d1c9f4a3b633386ad5e2")}).count()
        db.getCollection('DOCUMENT').update(
            {"BIZ_NAME.$id" : ObjectId("5624d1c9f4a3b633386ad5e2")},   
            {$set: {"BIZ_NAME.$id" : ObjectId("564c07aff4a3b6d5de8b2ec8")} }, 
            {multi: true});
        db.getCollection('RECEIPT').find({"BIZ_NAME.$id" : ObjectId("5624d1c9f4a3b633386ad5e2")}).count()
        db.getCollection('DOCUMENT').find({"BIZ_NAME.$id" : ObjectId("5624d1c9f4a3b633386ad5e2")}).count()
        
        db.getCollection('BIZ_STORE').find({"BIZ_NAME.$id":ObjectId("564c07aff4a3b6d5de8b2ec8")})        
        db.getCollection('RECEIPT').update(
            {"BIZ_NAME.$id" : ObjectId("564c07aff4a3b6d5de8b2ec8")},   
            {$set: {"BIZ_STORE.$id" : ObjectId("564c07b0f4a3b6d5de8b2ec9")} }, 
            {multi: true});
            
        db.getCollection('DOCUMENT').update(
            {"BIZ_NAME.$id" : ObjectId("564c07aff4a3b6d5de8b2ec8")},   
            {$set: {"BIZ_STORE.$id" : ObjectId("564c07b0f4a3b6d5de8b2ec9")} }, 
            {multi: true});    
        
        db.getCollection('BIZ_STORE').remove({"BIZ_NAME.$id":ObjectId("5624d1c9f4a3b633386ad5e2")})
        db.getCollection('BIZ_NAME').remove({N:"Paytm.com", _id:ObjectId("5624d1c9f4a3b633386ad5e2")})

- Update Biz Store Address 

        db.getCollection('BIZ_STORE').find({})
        db.getCollection('BIZ_STORE').find().snapshot().forEach(
          function (e) {           
            // update document, using its own properties
            e.FA = e.AD;
        
            // save the updated document
            db.getCollection('BIZ_STORE').save(e);
          }
        )
        db.getCollection('BIZ_STORE').find({})         

### Date Aug 30 2016 - Build 1819
Changed Field name from "CN" to "NO" for comment notes. NO is notes changed from CommentNotes

    db.getCollection('RECEIPT').updateMany({}, {$rename: { "CN": "NO" }})
    db.getCollection('DOCUMENT').updateMany({}, {$rename: { "CN": "NO" }})

### Date July 23 2016 - Build 1767
Update USER_PAID to USER and update field name from ULE to UL

    db.getCollection('USER_PROFILE').update({"ULE" : "USER_PAID"},   { $set: {"ULE": "USER"} }, {multi: true});   
    db.getCollection('USER_PROFILE').updateMany({}, {$rename: { "ULE": "UL" }})
    
Updated INVITE
     
    db.getCollection('INVITE').update({},            { $set: {UL: "USER"} },                  {multi: true});    

### Date July 17 2016 - Build 1749
Removed CN

    db.getCollection('REGISTERED_DEVICE').update({},            {$unset : {CN: ''}}, false, true);

### Date July 2 2016 - Build 1721
Caused because of inactive token.
    
    db.getCollection('REGISTERED_DEVICE').update(
            {},            
            {$set: {U: ISODate("2016-06-01T01:00:00.320Z")} },                  
            {multi: true}
        );

### Date June 27 2016 - Build 1702

    Upgrade ActiveMQ to 5.13.3
    Upgrade Mongo 3.2.7
    Delete MileageEntity collection
    Upgrade Jenkins

    db.getCollection('USER_PROFILE').update({"ULE" : "TECHNICIAN"},   { $set: {"ULE": 'TECH_RECEIPT'} }, {multi: true});    
    db.getCollection('MESSAGE_DOCUMENT').update({"ULE" : "TECHNICIAN"},   { $set: {"ULE": 'TECH_RECEIPT'} }, {multi: true});
    db.getCollection('CRON_STATS').update({"TN" : "Upload"},   { $set: {"TN": 'ReceiptUpload'} }, {multi: true});    

### Date June 16 2016 - Build 1678
Change 'Notes' to 'N' and "Recheck' to 'R'

    db.getCollection('COMMENT').update({"CT" : "RECHECK"},   { $set: {"CT": 'R'} }, {multi: true});
    db.getCollection('COMMENT').update({"CT" : "NOTES"},     { $set: {"CT": "N"} }, {multi: true});    

### Date May 26 2016 - Build 1647
Update co-ordinates and added index for co-ordinates
Expecting 9 record failure out of 504

    db.getCollection('BIZ_STORE').find().count();
    db.getCollection('BIZ_STORE').update({},            {$unset : {COR: ''}}, false, true);
    db.getCollection('BIZ_STORE').update({},            { $set: {EA: false} },                  {multi: true});

#### Date May 12 2016 - Build 1638
Simplified data to smaller name to save space.
    
    db.getCollection('fs.files').update(
        {'FILE_TYPE' : 'RECEIPT'},            
        {$set: {'FILE_TYPE': 'R'}},                  
        {multi: true}
    );
    
Added File Type to FILE_SYSTEM to support coupons.
 
    db.getCollection('FILE_SYSTEM').update(
        {},            
        {$set: {FT: 'R'} },                  
        {multi: true}
    );

#### Date Mar 27 2016 - Build 1585
    fixed index 'receipt_friend_reference_idx' by updating the field name from RF to RD (referredId) 

#### Date Dec 04 2015 - Build 1477
    db.getCollection('BIZ_STORE').update({},            { $set: {EA: false} },                  {multi: true});

#### Date Nov 16 2015 - Build 1456
Update records with BS as NB to Promotional for all the receipts

    db.getCollection('RECEIPT').find({"BS": {$ne : "P"}})
    db.getCollection('RECEIPT').update({},              { $set: {BS: 'P'} },                    {multi: true});
    db.getCollection('RECEIPT').update({"BS" : "NB"},   { $set: {BS: 'P', U : new ISODate()} }, {multi: true});
    db.getCollection('RECEIPT').update({},              { $set: {U : new ISODate()} },          {multi: true});

#### Date Nov 14 2015 - Build 1450
Delete Device Id for complete refresh for a user

    db.getCollection('REGISTERED_DEVICE').remove({"RID" : "10000000003"})

#### Date Oct 21 2015 - Build 1401
Update billing as it was causing issue when BS is NB and ABT is empty/null 

    db.getCollection('BILLING_HISTORY').update({}, { $set: {BS: 'P'} }, {multi: true});
    db.getCollection('BILLING_HISTORY').update({}, { $set: {ABT: 'P'} }, {multi: true});

#### Date Sept 30 2015 - Build 1352
Added split count and copied TOT and TAX to ST and SX    

    db.getCollection('RECEIPT').update({}, { $set: {SC: 1} }, {multi: true});
    db.getCollection('RECEIPT').update({}, { $set: {ST: 0} }, {multi: true});
    db.getCollection('RECEIPT').update({}, { $set: {SX: 0} }, {multi: true});
    
    db.getCollection('RECEIPT').find().forEach(
       function (elem) {
           db.getCollection('RECEIPT').update(
               {
                   _id: elem._id
               },
               {
                   $set: {
                       ST: elem.TOT,
                       SX: elem.TAX
                   }
               }
           );
       }
    );   
    
    Drop all ReceiptEntity index 

#### Date Aug 30 2015 - Build 1304
Added notify user field

    db.getCollection('DOCUMENT').update({}, { $set: {NU: false} }, {multi: true});
    
#### Date Jun 11 2015 - Build 1124
Removed BilledAccount boolean as was not being used
    
    db.getCollection('BILLING_ACCOUNT').update({}, {$unset : {'BA': ''}}, false, true);

#### Date Apr 29 2015 - Build 1030

Rename field name from UID to PUID

    db.getCollection('USER_PROFILE').update( {}, { $rename: { "UID": "PUID" } },  false, true)

#### Date Apr 23 2015 - Build 1004
Removed reminisce of file and FileSystem (Not sure why this happened). Needs to be investigated.
    db.getCollection('fs.files').remove({"_id" : ObjectId("552ad915036462c8689d434b")})
    db.getCollection('FILE_SYSTEM').remove({"BID" : "552ad915036462c8689d434b"})
    
    db.getCollection('RECEIPT').find(
    {
        "RID" : "10000000004", 
        "C" : { 
            "$gt" : ISODate("2015-04-11"), 
            "$lt" : ISODate("2015-04-13")
            }
    })

#### Date Apr 11 2015
Changing all records to active status from false

    db.getCollection('NOTIFICATION').update({}, {$set : {'A' : true}}, false, true)

#### Date Apr 5 2015
    db.getCollection('BIZ_STORE').update({}, {$unset : {'LAT': '', 'LNG': ''}}, false, true)

#### Date Mar 28 2015

    db.getCollection('ITEM').update({'TT' : 'NOT_TAXED'}, {$set : {'TT' : 'NT'}}, false, true)
    db.getCollection('ITEM').update({'TT' : 'TAXED'}, {$set : {'TT' : 'T'}}, false, true)
    db.getCollection('ITEM_OCR').update({'TT' : 'NOT_TAXED'}, {$set : {'TT' : 'NT'}}, false, true)
    db.getCollection('ITEM_OCR').update({'TT' : 'TAXED'}, {$set : {'TT' : 'T'}}, false, true)
    