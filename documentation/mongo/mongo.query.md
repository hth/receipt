
#####Query between two dates: 

    db.RECEIPT.find({
        'RTXD' : 
        { 
            "$gte" : ISODate('2015-01-04 00:00:00.000Z'), 
            "$lte" : ISODate('2015-01-06 00:00:00.000Z')
        }
    })
    
#####Update multi
    
    db.getCollection('ITEM').update({'TT' : 'NOT_TAXED'}, {$set : {'TT' : 'NT'}}, false, true)
    db.getCollection('ITEM').update({'TT' : 'TAXED'}, {$set : {'TT' : 'T'}}, false, true)