package main

import (
    "fmt"
    "net/http"
    "encoding/json"
    "io/ioutil"
)


func main() {

    resp, err := http.Get("http://localhost:9200/lookup/_search")
    if err != nil {
        panic(err)
    }
    defer resp.Body.Close()

    fmt.Println("Response status:", resp.Status)


    // Unmarshal or Decode the JSON to the interface
    body, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        panic(err.Error())
    }

    var result map[string]interface{}
    json.Unmarshal(body, &result)
    fmt.Printf("Results: %v\n", result["hits"])

}