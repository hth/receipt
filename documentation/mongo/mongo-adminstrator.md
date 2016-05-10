
### Added Admin User

    db.createUser(
      {
        user: "fukrey",
        pwd: "0*Shark3atStark!",
        roles: [ { role: "userAdminAnyDatabase", db: "rm-test" } ]
      }
    )

    use admin
    db.auth("fukrey", "0*Shark3atStark!" )

### Drop User

    db.dropUser("fukrey", {w: "majority", wtimeout: 5000})
    

    db.createUser(
      {
        user: "hitender",
        pwd: "dbMail3*!",
        roles: [ { role: "readAnyDatabase", db: "rm-test" } ]
      }
    );
    
    use rm-test
    db.createUser( 
        { 
            "user"  : "hitender",
            "pwd"   : "dbMail3*!",
            "customData" : { employeeId: 2 },
            "roles" : [ 
                { role: "clusterAdmin", db: "admin" },
                { role: "readAnyDatabase", db: "admin" },
                "readWrite"
            ] 
        },
        { w: "majority" , wtimeout: 5000 } 
    )    