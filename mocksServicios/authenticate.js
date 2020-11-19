fs = require('fs');


module.exports = [
{
  path: '/integrationTest/authenticate',
  method: 'POST',
  template: "authToken"

},
  {
    path: '/mocksBackend/authenticate',
    method: 'POST',
    template: "authToken"

  }
]


