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
    