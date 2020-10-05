using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PlayerCam : MonoBehaviour
{
    public Vector3 pos;
    public Vector3 angle;
    public float smooth = .125f;
    public float time;
    public Transform player;

    void LateUpdate()
    {
        Vector3 nextPos = player.position + player.rotation.normalized*pos;
        Vector3 smoothed = Vector3.Lerp(transform.position, nextPos, smooth);
        transform.position = smoothed;
        transform.LookAt(player.position + player.rotation.normalized*angle);
    }
}
