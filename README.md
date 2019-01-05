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
        "id" : "2"    // user_account_id
        "phone_number" : "123-456-789"    // 전화번호
        "name" : "수수수"                  // 전화번호에 해당하는 이름
    }

    
/3. response :  json

  ex)

    {
    "id": 48,                            // 생성된 연락처의 id (프론트 구현에는 필요 없음)
    "name": "태수킴",                     // 서버 연락처에 저장한 이름
    "account": 3,                           // user_account_id
    "number": "010-4564-6874"               // 서버 연락처에 저장한 
    }

 /4. request

    url : socrip3.kaist.ac.kr:9380/contact/<account_id>
    ex> socrip3.kaist.ac.kr:9380/contact/3

    method : GET

    Content-Type : application/json

    body : none

/3. response :  json array

  ex)

    [
    {
        "id": 5,
        "name": "김태",
        "phone_number": "654010-1234-5678",
        "account": 2
    },
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
    }
    ]
