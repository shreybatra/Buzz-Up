exports = async function(data) {
  /*
    A Scheduled Trigger will always call a function without arguments.
    Documentation on Triggers: https://docs.mongodb.com/stitch/triggers/overview/

    Functions run by Triggers are run as System users and have full access to Services, Functions, and MongoDB Data.

    Accessing a mongodb service:
    var collection = context.services.get("mongodb-atlas").db("db_name").collection("coll_name");
    var doc = collection.findOne({ name: "mongodb" });

    To call other named functions:
    var result = context.functions.execute("function_name", arg1, arg2);
  */
  
  var msg = "Greetings from BUZZ UP, MongoDB Stitch powered Trigger\
          You have the following new BUZZ going on FIRE near you -"
          + data.topic_name + "Christmas Party\
        Open your Buzz Up app now to get connected with this BUZZ..!! Don't be too late or the Buzz would expire..!!\
        Team Buzz Up";
  
  var collection = context.services.get("mongodb-atlas").db("mongohack").collection("users");
  
  var users = await collection.find().toArray();
  
  const http = context.services.get("myHttp");
  obj = {
    'email':users,
    'message': msg
  };
  
  return http.post({
      url: "https://prod-10.southeastasia.logic.azure.com:443/workflows/7b78442f00704017831425f90ffcef07/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=oNgUWwSG1yLOIyl6ozSl_MVtzUdsMDFAuyXKgaJaZ30",
      body: obj,
      encodeBodyAsJSON: true
    })
    .then(response => {
      // The response body is encoded as raw BSON.Binary. Parse it to JSON.
      const ejson_body = EJSON.parse(response.body.text());
      return ejson_body;
    });
  
};
