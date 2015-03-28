
#####Query between two dates: 

    db.RECEIPT.find({
        'RTXD' : 
        { 
            "$gte" : ISODate('2015-01-04 00:00:00.000Z'), 
            "$lte" : ISODate('2015-01-06 00:00:00.000Z')
        }
    })