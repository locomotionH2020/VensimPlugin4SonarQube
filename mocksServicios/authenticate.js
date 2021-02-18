fs = require('fs');


module.exports = [
{
  path: '/integrationTest/authenticate',
  method: 'POST',
  template: "authToken"

},
  {
    path: '/authenticate',
    method: 'POST',
    template: "authToken"

  }
]


