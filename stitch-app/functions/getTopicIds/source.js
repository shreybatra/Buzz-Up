exports = function(data){
  /*
    Accessing application's values:
    var x = context.values.get("value_name");

    Accessing a mongodb service:
    var collection = context.services.get("mongodb-atlas").db("dbname").collection("coll_name");
    var doc = collection.findOne({owner_id: context.user.id});

    To call other named functions:
    var result = context.functions.execute("function_name", arg1, arg2);

    Try running in the console below.
  */
  
  // console.log(typeof(data[0]));
  
  console.log(data);
  data = JSON.parse(data);
  console.log(data);
  
  
  
  // console.log(JSON.parse(data.lng));
  
  var date = new Date();
  
  const collection = context.services.get("mongodb-atlas").db("mongohack").collection("topics");
  
  // return collection.find().toArray();
  
  const agg_pipeline = [
    {
      $match: {
        location: { $geoWithin: { $centerSphere: [ [ data.lng, data.lat ], data.radius/3963.2 ] } },
        active_till_date: {
          $gte: date
        }
      }
    },
    {
      $project: {
        '_id':1,
        'topic_name':1,
        'topic_count':1
      }
    },
    {
      $sort: {
        'topic_count':-1
      }
    }
  ];
  	
  	if(data.text_search!=='')
  	{
  	  agg_pipeline[0].$match.topic_name = {
  			'$regex': data.text_search,
  			'$options': 'i'
  		};
  	}
  	
  
  
  // var arr = [];
  
  return collection.aggregate(agg_pipeline).toArray();
  
  
};