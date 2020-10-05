using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Move : MonoBehaviour
{
    public float rotX;
    public float sens;
    public Rigidbody body;
    private int climbing; // Time that you can climb for
    public tags playerTags;
    public float move;
    public float jump;
    public float maxSpeed;
    public float maxJump;
    public bool climbStarted = false;
    public float climbStartTime = 0;
    private Vector3 normal;
    void Start()
    {
        Cursor.visible = false;
        Cursor.lockState = CursorLockMode.Locked;
        climbing = 3;
    }
    // Update is called once per frame
    void FixedUpdate()
    {
        rotX += Input.GetAxis("Mouse X")*sens;
        transform.rotation = Quaternion.Euler(0, rotX, 0);
        if(Input.anyKey)
        {
            if(Input.GetKey(KeyCode.W)&&!Input.GetKey(KeyCode.S))
            {
                if(body.velocity.magnitude <= maxSpeed)
                {
                    if(playerTags.has("grounded") && !playerTags.has("climbing"))
                    {
                        body.AddForce(body.transform.forward.normalized*move);
                    }
                    else if(playerTags.has("climbing") && climbing > 0) // Here we climb if we are touching an object with our nose
                    { 
                        if(climbStarted == false || climbStartTime + 1 < Time.time)
                        {
                            Debug.Log(climbing);
                            climbStarted = true;
                            climbStartTime = Time.time;
                            climbing--;
                        }
                        body.AddForce(body.transform.up.normalized*move);
                    }
                    if(!playerTags.has("climbing"))
                    {
                        climbStarted = false;
                    }
                    if(!playerTags.has("grounded") && !playerTags.has("climbing"))
                    {
                        body.AddForce(body.transform.forward.normalized*move/4);
                    }
                }
            }
            else if(Input.GetKey(KeyCode.S)&&!Input.GetKey(KeyCode.W))
            {
                if(body.velocity.magnitude >= -maxSpeed)
                {
                    if(playerTags.has("grounded"))
                        body.AddForce(body.transform.forward.normalized*-move);
                    else
                    {
                        body.AddForce(body.transform.forward.normalized*-move/3);
                    }
                }
            }
            if(Input.GetKey(KeyCode.Space))
            {
                if(body.velocity.y <= maxJump && playerTags.has("grounded"))
                {
                    body.AddForce(body.transform.up*jump);
                }
            }
        }
        if(playerTags.has("grounded") && climbing < 3)
        {
            if(climbStartTime + 1 < Time.time)
            {
                climbing++;
            }
        }
    }
}
