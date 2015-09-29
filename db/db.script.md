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
    