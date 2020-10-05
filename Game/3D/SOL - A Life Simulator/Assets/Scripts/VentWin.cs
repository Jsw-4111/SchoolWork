using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class VentWin : MonoBehaviour
{
    public GameObject playerObj;
    void OnTriggerEnter(Collider other)
    {
        if(Equals(other.gameObject, playerObj))
            Debug.Log("You win!");
    }
}
