# pocketmon

<> 는 <> 안에 해당하는 값을 넣어줘야해요

/1. request 

    url : socrip3.kaist.ac.kr:9380/account/

    method : POST

    Content-Type : application/json

    body : 

      ex)

        {
          "account" : "asdfasddf"
        }
        
/1. response :  json data

      ex)

        {
          "is_first": false,          // 처음 접속한 계정인지 아닌지
          "account": "asdfasddf",     // 구글 account
          "pk": 2                     // 전역변수로 사용할 pk
        }
  
  
/2. request

    url : socrip3.kaist.ac.kr:9380/contact/<user_id>

    method : GET

    Content-Type : application/json

    body : none
    

/2. response : json array

  ex)

    [
      {
          "id": 3,
          "name": "태수",
          "phone_number": "010-1234-5678",
          "account": 2
      },
      {
          "id": 4,
          "name": "태수",
          "phone_number": "010-1234-5678",
          "account": 2
      },
      {
          "id": 5,
          "name": "김태",
          "phone_number": "654010-1234-5678",
          "account": 2
      }
    ]
  
 /3. request

    url : socrip3.kaist.ac.kr:9380/contact/

    method : POST

    Content-Type : application/json

    body : json
    ex)

{
    "id" : "2"
    "phone_number" : "123-456-789"
    "name" : "수수수"
}

    
/3. response :  json array

  ex)

    [
      {
          "id": 3,
          "name": "태수",
          "phone_number": "010-1234-5678",
          "account": 2
      },
      {
          "id": 4,
          "name": "태수",
          "phone_number": "010-1234-5678",
          "account": 2
      },
      {
          "id": 5,
          "name": "김태",
          "phone_number": "654010-1234-5678",
          "account": 2
      }
    ]
