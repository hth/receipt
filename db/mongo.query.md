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