using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class isClimbing : MonoBehaviour
{
    public tags playerTags;
    public GameObject climbingObj;
    void OnTriggerEnter(Collider other)
    {
        playerTags.add("climbing");
    }

    void OnTriggerExit(Collider other)
    {
        playerTags.remove("climbing");
    }
}
