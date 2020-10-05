using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Grounded : MonoBehaviour
{
    public GameObject playerObj;
    public tags playerTags;
    void OnTriggerStay(Collider other)
    {
        playerTags.add("grounded");
    }

    void OnTriggerExit(Collider other)
    {
        playerTags.remove("grounded");
    }
}
