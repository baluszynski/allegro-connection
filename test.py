import requests
import json
import time

CLIENT_ID = "b6e830d1676144f78ea9e0b5f850e11c"          # wprowadź Client_ID aplikacji
CLIENT_SECRET = "BTfuNFAtEIYGIMh4Q5Y1euMqCBW2HmBgnQjaL5izWp9SY7y5GeBSQaIqdHiFTARt"      # wprowadź Client_Secret aplikacji
CODE_URL = "https://allegro.pl/auth/oauth/device"
TOKEN_URL = "https://allegro.pl/auth/oauth/token"


def get_code():
    try:
        payload = {'client_id': CLIENT_ID}
        headers = {'Content-type': 'application/x-www-form-urlencoded'}
        api_call_response = requests.post(CODE_URL, auth=(CLIENT_ID, CLIENT_SECRET),
                                          headers=headers, data=payload, verify=False)
        print(api_call_response.request.body)
        print(30*"1")
        print(api_call_response)
        print(30*"2")
        return api_call_response
    except requests.exceptions.HTTPError as err:
        raise SystemExit(err)


def get_access_token(device_code):
    try:
        headers = {'Content-type': 'application/x-www-form-urlencoded'}
        data = {'grant_type': 'urn:ietf:params:oauth:grant-type:device_code', 'device_code': device_code}
        print()
        api_call_response = requests.post(TOKEN_URL, auth=(CLIENT_ID, CLIENT_SECRET),
                                          headers=headers, data=data, verify=False)
        print(api_call_response.request.body)
        return api_call_response
    except requests.exceptions.HTTPError as err:
        raise SystemExit(err)


def await_for_access_token(interval, device_code):
    while True:
        time.sleep(interval)
        result_access_token = get_access_token(device_code)
        token = json.loads(result_access_token.text)
        if result_access_token.status_code == 400:
            if token['error'] == 'slow_down':
                interval += interval
            if token['error'] == 'access_denied':
                break
        else:
            return token['access_token']


def main():
    code = get_code()
    # print(code)
    result = json.loads(code.text)
    # print(result)
    print("User, open this address in the browser:" + result['verification_uri_complete'])
    access_token = await_for_access_token(int(result['interval']), result['device_code'])
    print("access_token = " + access_token)


if __name__ == "__main__":
    main()