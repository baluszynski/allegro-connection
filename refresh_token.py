import requests
import json

CLIENT_ID = "b6e830d1676144f78ea9e0b5f850e11c"          # wprowadź Client_ID aplikacji
CLIENT_SECRET = "BTfuNFAtEIYGIMh4Q5Y1euMqCBW2HmBgnQjaL5izWp9SY7y5GeBSQaIqdHiFTARt"      # wprowadź Client_Secret aplikacji
REDIRECT_URI = "http://localhost:8000"       # wprowadź redirect_uri
TOKEN_URL = "https://allegro.pl/auth/oauth/token"

def get_next_token(token):
    try:
        data = {'grant_type': 'refresh_token', 'refresh_token': token, 'redirect_uri': REDIRECT_URI}
        access_token_response = requests.post(TOKEN_URL, data=data, verify=False,
                                              allow_redirects=False, auth=(CLIENT_ID, CLIENT_SECRET))
        tokens = json.loads(access_token_response.text)
        return tokens['access_token'], tokens['refresh_token']
    except requests.exceptions.HTTPError as err:
        raise SystemExit(err)


def main():
    with open('refresh_token.txt', 'r') as file:
        refresh_token = file.read()
    access_token, refresh_token = get_next_token(refresh_token)
    with open('refresh_token.txt', 'w') as file:
        file.write(refresh_token)
    with open('access_token.txt', 'w') as file:
        file.write(access_token)


if __name__ == "__main__":
    main()