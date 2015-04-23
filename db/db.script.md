#### Date Apr 23 2015 - Build 1000
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