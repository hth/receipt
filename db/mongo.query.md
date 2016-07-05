## Find Receipt for specific FILE_SYSTEM

    db.getCollection('RECEIPT').find({"FS.$id": ObjectId("55d8ff2ff4a3b69fab11138e")})

## Delete account related 

    db.getCollection('BILLING_ACCOUNT').remove({"RID" : "10000000381"})
    db.getCollection('BILLING_HISTORY').remove({"RID" : "10000000381"})
    db.getCollection('EMAIL_VALIDATE').remove({"RID" : "10000000381"})
    db.getCollection('EXPENSE_TAG').remove({"RID" : "10000000381"})
    db.getCollection('NOTIFICATION').remove({"RID" : "10000000381"})
    
    db.getCollection('USER_PROFILE').remove({"RID" : "10000000381"})
    db.getCollection('USER_PREFERENCE').remove({"RID" : "10000000381"})
    db.getCollection('USER_AUTHENTICATION').remove({"_id" : ObjectId("576add45f4a3b614d3144ed7")})
    db.getCollection('USER_ACCOUNT').remove({"RID" : "10000000381"})

## For renaming fields

    db.events.update({}, {$rename: {'lat': 'coords.lat', 'lon': 'coords.lon'}}, false, true)

## For updating the document structure

    db.events.find().snapshot().forEach(
      function (e) {
        // update document, using its own properties
        e.coords = { lat: e.lat, lon: e.lon };
    
        // remove old properties
        delete e.lat;
        delete e.lon;
    
        // save the updated document
        db.events.save(e);
      }
    )

## Find User with Biz Name

    db.getCollection('RECEIPT').find(  
        {
            "RID" : "10000000147", "BIZ_NAME.$id" : ObjectId('57029f44f4a3b6aed3fa2221')
        }
    )

## Using And with multiple OR with showing selected field    
    
    db.getCollection('RECEIPT').find(    
        {
            $and : [
                {$or : [ {"_id" : new ObjectId("56c53296939200f6d6b6661b")}, {"RF" : "56c53296939200f6d6b6661b"}, {"A" : true}]},
                {$or : [ {"A" : true}]}
            ]}, 
        {
            "SC" : 1, "A" : 1, "RID" : 1
        }
    )